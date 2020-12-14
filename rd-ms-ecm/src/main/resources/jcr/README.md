# How to update language file with new content from phrase export

 1. Change workdir to `cms/src/main/resources/jcr`.
 1. run `docker run --rm --workdir /app -v $PWD:/app python:3-slim python /app/scripts/merge_content_into_language.py <language_code> path/to/update-file.json
