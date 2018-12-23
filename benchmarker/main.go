package main

import (
	"encoding/json"
	"fmt"
	"log"
	"math"
	"math/rand"
	"net/http"
	"strings"
	"time"

	"github.com/jessevdk/go-flags"
)

type User struct {
	ID   string `json:"id"`
	Name string `json:"name"`
}

func main() {
	var opts struct {
		Qps int `long:"qps" default:"10"`
	}

	args, err := flags.Parse(&opts)
	if err != nil {
		log.Fatal(err)
	}

	if len(args) == 0 {
		log.Fatal("origin is not specified")
	}

	origin := args[0]
	fmt.Println(origin)
	ch := make(chan struct{}, opts.Qps*60)

	for {
		log.Printf("current tasks: %d", len(ch))

		begin := time.Now()

		for i := 0; i < opts.Qps; i++ {
			go func(origin string) {
				ch <- struct{}{}
				time.Sleep(time.Duration(float64(time.Second) * (math.Pow(1.2, float64(rand.Intn(1000))/100) - 1)))
				runSenario(origin)
				<-ch
			}(origin)
		}

		elapsed := time.Now().Sub(begin)
		time.Sleep(1*time.Second - elapsed)

	}
}

func runSenario(origin string) {
	user := taskUserPostCreate(origin)
	if user == nil {
		return
	}

	taskUserGetSelf(origin, user.ID)
}

func taskUserPostCreate(origin string) *User {
	res, err := http.Post(origin+"/users", "application/json", strings.NewReader(`{"name":"jero"}`))
	if err != nil {
		log.Printf("got error: %s", err)
		return nil
	}

	if res.StatusCode != 201 {
		log.Printf("got unexpected status code: %d", res.StatusCode)
		return nil
	}

	u := User{}
	err = json.NewDecoder(res.Body).Decode(&u)
	if err != nil {
		log.Printf("failed to parse response: %s", err)
		return nil
	}

	if u.ID == "" || u.Name == "" {
		log.Printf("assertion error")
		return nil
	}

	return &u
}

func taskUserGetSelf(origin string, id string) {
	res, err := http.Get(origin + "/users/" + id)
	if err != nil {
		log.Printf("got error: %s", err)
		return
	}

	if res.StatusCode != 200 {
		log.Printf("got unexpected status code: %d", res.StatusCode)
		return
	}

	u := User{}
	err = json.NewDecoder(res.Body).Decode(&u)
	if err != nil {
		log.Printf("failed to parse response: %s", err)
		return
	}

	if u.ID == "" || u.Name == "" {
		log.Printf("assertion error")
		return
	}
}
