#!/usr/bin/env bash
set -Eeuo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
API_DIR="$SCRIPT_DIR/api"

if ! command -v java >/dev/null 2>&1; then
	printf '%s\n' "Error: Java no está disponible en PATH." >&2
	exit 1
fi

if [[ ! -x "$API_DIR/mvnw" ]]; then
	printf '%s\n' "Error: no se encontró api/mvnw o no tiene permisos de ejecución." >&2
	exit 1
fi

cd "$SCRIPT_DIR"
case "$(uname -s)" in
	MINGW*|MSYS*|CYGWIN*)
		start index.html
		;;
	Linux*)
		xdg-open index.html
		;;
	Darwin*)
		open index.html
		;;
	*)
		printf '%s\n' "Error: sistema operativo no compatible para abrir el dashboard." >&2
		exit 1
		;;
esac

printf '%s\n' "Iniciando API Spring Boot en http://localhost:8080 ..."
cd "$API_DIR"
exec ./mvnw spring-boot:run
