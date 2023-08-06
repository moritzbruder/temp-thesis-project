# Thesis Project: The divisible monolith

This is a sample project built to evaluate an architectural approach that I explored in my bachelor thesis.
The idea was to create a monolithic project that has artificial borders like a microservice-based system would have in order to combine the benefits of monoliths and microservices, especially in early stage companies and projects.

## The application
This application is a webservice that allows the creation of places (like restaurants, shops, parks, etc.) and activities (things that can be done at said locations) and could be used to create a platform of activity ideas in an area.

The api is documented in the [openapi spec](docs/openapi.yml) (previewable in [swagger editor](https://editor.swagger.io/))

## How to run

Dependencies are managed using gradle, the [Makefile](Makefile) contains commands for starting a database using podman (this is required both for running locally and tests) as well as running the service and its tests.

## The structure explained briefly

The [`services`](src/services) package contains a package for each conceptual service:
`auth`, `activities`, `images`, and `places`

Each contains a number of actions (use cases for the service) that are either exposed via:
- The HTTP Api defined in services `api/*Routes.kt` file to api clients.
- The `*ServiceInterface.kt` interface to other services.

This structure would also allow developers to separate services in the future by writing an adapter that implements the `*ServiceInterface` and uses some form of communication (http, websockets, rpc, etc.) to connect the services.