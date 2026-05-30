#!/bin/bash
# Incrementa la versión en version.properties basándose en etiquetas de Git.
# Uso: bash scripts/bump_version.sh [patch|minor|major]

PROPS_FILE="version.properties"
BUMP_TYPE=${1:-"patch"}

cd "$(dirname "$0")/.."

git fetch --tags

LATEST_TAG=$(git describe --tags --abbrev=0 2>/dev/null)

if [ -z "$LATEST_TAG" ]; then
    echo "No hay tags, usando version.properties como fallback."
    VERSION_NAME=$(grep 'versionName=' "$PROPS_FILE" | cut -d'=' -f2)
    VERSION_CODE=$(grep 'versionCode=' "$PROPS_FILE" | cut -d'=' -f2)
else
    echo "Último tag: $LATEST_TAG"
    VERSION_NAME=${LATEST_TAG#v}
    VERSION_CODE=$(grep 'versionCode=' "$PROPS_FILE" | cut -d'=' -f2)
fi

echo "Versión actual: $VERSION_NAME (código $VERSION_CODE)"

NEW_VERSION_CODE=$((VERSION_CODE + 1))

IFS='.' read -ra PARTS <<< "$VERSION_NAME"
MAJOR=${PARTS[0]:-1}
MINOR=${PARTS[1]:-0}
PATCH=${PARTS[2]:-0}

case $BUMP_TYPE in
    major) MAJOR=$((MAJOR + 1)); MINOR=0; PATCH=0 ;;
    minor) MINOR=$((MINOR + 1)); PATCH=0 ;;
    patch) PATCH=$((PATCH + 1)) ;;
    *) echo "Tipo desconocido: $BUMP_TYPE. Usando patch."; PATCH=$((PATCH + 1)) ;;
esac

NEW_VERSION_NAME="$MAJOR.$MINOR.$PATCH"

printf 'versionCode=%s\nversionName=%s\n' "$NEW_VERSION_CODE" "$NEW_VERSION_NAME" > "$PROPS_FILE"

echo "Nueva versión: $NEW_VERSION_NAME (código $NEW_VERSION_CODE)"
