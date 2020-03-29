#!/bin/sh

main() {
    cd "${1}"
    find . -maxdepth 1 -type d | while read folder
    do
        main "${folder}"
    done

    find . -maxdepth 1 -type f | while read movieFile
    do
        echo "${movieFile}" | grep -Ei '.*\.(mov|mp4|m4v)$' >/dev/null
        if [ $? -eq 0 ]
        then
            filename=$(basename -- "$movieFile")
            extension="${filename##*.}"
            filename="${filename%.*}"
            if [ -f "/pictures/${filename}.jpg" ]
            then
                echo pass ${movieFile} >/dev/null
            else
                echo /usr/local/bin/ffmpeg -i "${movieFile}" -vframes 1 -an -s 720x399 -ss 30 "/pictures/${filename}.jpg"
            fi
        fi
    done

    cd ..
}

saveFolder="$(pwd)"
main "/movies/"
cd "${saveFolder}"
