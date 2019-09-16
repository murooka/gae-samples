package main

import (
	"log"
	"net/http"
	"os"
	"os/signal"
	"sort"
	"syscall"
	"time"

	flags "github.com/jessevdk/go-flags"
)

type User struct {
	ID   string `json:"id"`
	Name string `json:"name"`
}

type Reports struct {
	Count   int
	Average time.Duration
	P50     time.Duration
	P90     time.Duration
	P95     time.Duration
}

func main() {
	var opts struct {
		Concurrency int `short:"c" long:"concurrency" default:"10"`
	}

	args, err := flags.Parse(&opts)
	if err != nil {
		log.Fatal(err)
	}

	if len(args) == 0 {
		log.Fatal("origin is not specified")
	}

	origin := args[0]
	log.Printf("origin: %s", origin)

	quitCh := make(chan struct{})
	reportCh := make(chan time.Duration, 1000)

	c := 0
	for ; c < opts.Concurrency; c++ {
		go func(origin string) {
			worker(origin, quitCh, reportCh)
		}(origin)
	}

	log.Printf("%d workers are running", c)

	signalCh := make(chan os.Signal)
	signal.Notify(signalCh, syscall.SIGINT, syscall.SIGHUP, syscall.SIGTERM, syscall.SIGQUIT)

MAIN_LOOP:
	for {
		select {
		case s := <-signalCh:
			log.Print(s)
			switch s {
			case syscall.SIGINT:
				for i := 0; i < c; i++ {
					go func(origin string) {
						worker(origin, quitCh, reportCh)
					}(origin)
				}
				c *= 2
				log.Printf("%d workers are running", c)
			case syscall.SIGHUP, syscall.SIGTERM, syscall.SIGQUIT:
				break MAIN_LOOP
			}
		case <-time.After(1 * time.Second):
			reports := aggregateReport(reportCh)
			log.Printf(
				"count:%d\taverage:%d\tp50:%d\tp90:%d\tp95:%d",
				reports.Count,
				reports.Average/time.Millisecond,
				reports.P50/time.Millisecond,
				reports.P90/time.Millisecond,
				reports.P95/time.Millisecond)
		}
	}

	log.Print("will quit")
}

func aggregateReport(reportCh chan time.Duration) Reports {
	reports := make([]int64, 0)
LOOP:
	for {
		select {
		case r := <-reportCh:
			reports = append(reports, int64(r))
		default:
			break LOOP
		}
	}
	sort.Slice(reports, func(i, j int) bool { return reports[i] < reports[j] })

	count := len(reports)
	if count == 0 {
		return Reports{}
	}

	var sum int64 = 0
	for _, r := range reports {
		sum += r
	}

	return Reports{
		Count:   count,
		Average: time.Duration(sum / int64(count)),
		P50:     time.Duration(reports[(count-1)*50/100]),
		P90:     time.Duration(reports[(count-1)*90/100]),
		P95:     time.Duration(reports[(count-1)*95/100]),
	}
}

func worker(origin string, quitCh chan struct{}, reportCh chan time.Duration) {
	for {
		select {
		case <-quitCh:
			break
		default:
			begin := time.Now()
			runScenario(origin)
			elapsed := time.Now().Sub(begin)

			reportCh <- elapsed
		}
	}
}

// func main() {
// 	var opts struct {
// 		Qps int `long:"qps" default:"10"`
// 	}
//
// 	args, err := flags.Parse(&opts)
// 	if err != nil {
// 		log.Fatal(err)
// 	}
//
// 	if len(args) == 0 {
// 		log.Fatal("origin is not specified")
// 	}
//
// 	origin := args[0]
// 	fmt.Println(origin)
// 	ch := make(chan struct{}, opts.Qps*60)
//
// 	for {
// 		log.Printf("current tasks: %d", len(ch))
//
// 		begin := time.Now()
//
// 		for i := 0; i < opts.Qps; i++ {
// 			go func(origin string) {
// 				ch <- struct{}{}
// 				time.Sleep(time.Duration(float64(time.Second) * (math.Pow(1.2, float64(rand.Intn(1000))/100) - 1)))
// 				runScenario(origin)
// 				<-ch
// 			}(origin)
// 		}
//
// 		elapsed := time.Now().Sub(begin)
// 		time.Sleep(1*time.Second - elapsed)
//
// 	}
// }

func runScenario(origin string) {
	taskGetOK(origin)
}

func taskGetOK(origin string) {
	res, err := http.Get(origin + "/ok")
	if err != nil {
		log.Printf("got error: %s", err)
		return
	}

	if res.StatusCode != 200 {
		log.Printf("got unexpected status code: %d", res.StatusCode)
		return
	}
}
