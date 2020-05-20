# Slink - The Link Shortener

![Build Status](https://github.com/digi0ps/slink/workflows/Clojure%20CI/badge.svg)

## About
Slink is a simple link shortener, built as a hobby project built using
production standards to dwelve deeper into Clojure apart from the stuff
I do at work.

Clojure is a beautiful functional programming language following the 
style of LISP. Working in it gave me a completely different (and better)
programming experience owing to it's list type functional syntax and
immutability.

Few snags I found while working with Clojure was the relatively short and small
documentation for even some of the popular packages. But this made me 
debug deeper by reading through the source code, so I can't really complain about this. :D


## Installation
1. Install [Leiningen](https://leiningen.org/)
2. Clone this repository and cd inside.
3. Run `lein deps`
4. Run `lein trampoline run` to launch the server or `lein uberjar` to package a jar.

## Endpoints
- `GET /`: Hello World
- `GET /api/links`: Get all slinks stored for an user
- `PUT /api/links`: Create a short link
- `GET /swagger/docs`: Swagger Docs for the API

## Geek stats
### API Front
The amazing [Ring](https://github.com/ring-clojure/ring) libary is being used for running the 
server and [Reitit](https://github.com/metosin/reitit) is being used for composing
routes and Swagger for the server.

### Config Management
Configuration management is being done using [Clonfig](https://github.com/mccraigmccraig/clonfig) which merges
the default EDN file (found in `resources/config.edn`) and environment
variables.

### Database + Caching
A Postgres instance is being used with [Toucan](https://github.com/metabase/toucan)
to simplify database queries and [Ragtime](https://github.com/weavejester/ragtime)
being used for migrations. Database code resides in `src/slink/db`.

Created short slinks are also being cached in Redis.

### Logging + Error Reporting
[Timbre](https://github.com/ptaoussanis/timbre) is being used for logging data.

Errors are reported to a personal Slack channel by utilising it's webhook feature. :D
## Links
- [Backend Server](https://slink-staging.herokuapp.com/)
- [Frontend Website](https://shortenyourlink.netlify.com/)

## Further down the road
- [ ] Code Coverage
- [ ] Dockerisation
- [ ] User Tables
- [ ] JWT Authentication
- [ ] Analytics per link
