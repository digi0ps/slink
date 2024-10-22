# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## 0.5.0 - 2020-04-12
### Added
- Swagger docs at `/swaggers/docs`.
- Any server error gets posted on Slack.
### Modified
- Redis to use heroku configuration.

## 0.4.1 - 2020-04-09
### Added
- Use Redis to cache hash->url.
### Modified
- Postgres connection to use connection pooling.

## 0.4.0 - 2020-04-04
### Added
- Added create link endpoint.
- Added get all links by user endpoint.
- Added redirect for hashs.

## 0.3.0 - 2020-03-26
### Added
- Added database setup utils.

## 0.2.0 - 2020-03-26
### Added
- Content type is now application/json for all endpoints.

## 0.1.0 - 2020-03-25
### Added
- Home endpoint which says a simple hello.
