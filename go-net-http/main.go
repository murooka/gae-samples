package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strings"

	"google.golang.org/appengine"
	"google.golang.org/appengine/datastore"
	"google.golang.org/appengine/log"
)

const (
	KindUser = "User"
)

type User struct {
	Name string
}

type UserView struct {
	ID   string `json:"id"`
	Name string `json:"name"`
}

func main() {
	http.HandleFunc("/users/", handleUser)
	http.HandleFunc("/users", handleUsers)
	http.HandleFunc("/", handle)
	appengine.Main()
}

func handleUsers(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case http.MethodPost:
		handleUserPostCreate(w, r)
	default:
		w.WriteHeader(http.StatusMethodNotAllowed)
	}
}

func handleUser(w http.ResponseWriter, r *http.Request) {
	id := strings.TrimPrefix(r.URL.Path, "/users/")
	switch r.Method {
	case http.MethodGet:
		handleUserGetSelf(w, r, id)
	default:
		w.WriteHeader(http.StatusMethodNotAllowed)
	}
}

func handleUserGetSelf(w http.ResponseWriter, r *http.Request, id string) {
	ctx := appengine.NewContext(r)
	key, err := datastore.DecodeKey(id)
	if err != nil {
		w.WriteHeader(http.StatusNotFound)
		return
	}

	u := User{}
	err = datastore.Get(ctx, key, &u)
	if err == datastore.ErrNoSuchEntity {
		w.WriteHeader(http.StatusNotFound)
		return
	} else if err != nil {
		log.Warningf(ctx, "failed to get data: %s", err)
		w.WriteHeader(http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	err = json.NewEncoder(w).Encode(UserView{
		ID:   key.Encode(),
		Name: u.Name,
	})
	if err != nil {
		log.Warningf(ctx, "failed to write response: %s", err)
		return
	}
}

func handleUserPostCreate(w http.ResponseWriter, r *http.Request) {
	ctx := appengine.NewContext(r)
	var body struct {
		Name string `json:"name"`
	}
	err := json.NewDecoder(r.Body).Decode(&body)
	if err != nil {
		log.Warningf(ctx, "failed to decode body: %s", err)
		w.WriteHeader(http.StatusInternalServerError)
		return
	}

	if body.Name == "" {
		log.Infof(ctx, "empty name")
		w.WriteHeader(http.StatusBadRequest)
		return
	}

	u := &User{
		Name: body.Name,
	}
	key, err := datastore.Put(ctx, datastore.NewIncompleteKey(ctx, KindUser, nil), u)
	if err != nil {
		log.Warningf(ctx, "failed to put data: %s", err)
		w.WriteHeader(http.StatusInternalServerError)
		return
	}

	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	err = json.NewEncoder(w).Encode(UserView{
		ID:   key.Encode(),
		Name: u.Name,
	})
	if err != nil {
		log.Warningf(ctx, "failed to write response: %s", err)
		return
	}
}

func handle(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintln(w, "Hello, world!")
}
