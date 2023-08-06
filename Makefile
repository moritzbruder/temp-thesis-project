database:
	podman container rm -fa && podman kill postgres || true
	podman run --rm --name postgres -e POSTGRES_PASSWORD=password -d -p 5432:5432 postgres:12.8-alpine

tests:
	./gradlew test

run-local:
	./gradlew run