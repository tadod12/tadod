#!/bin/sh

start_date="2020-01-27"
end_date="2020-02-01"

current_date="$start_date"

while [ "$(date -d "$current_date" +%s)" -le "$(date -d "$end_date" +%s)" ]; do
    echo "=============== Running for date: $current_date ==============="
    sh /var/cleaning/template.sh YellowClean /var/cleaning/yellow.properties "$current_date"
    sh /var/curation/template.sh YellowGeneral /var/curation/yellow.properties "$current_date"
    sh /var/curation/template.sh YellowMartLocation /var/curation/yellow.properties "$current_date"
    sh /var/curation/template.sh YellowMartPayment /var/curation/yellow.properties "$current_date"
    sh /var/curation/template.sh YellowMartRateCode /var/curation/yellow.properties "$current_date"
    sh /var/curation/template.sh YellowMartVendor /var/curation/yellow.properties "$current_date"
    echo "=============== Done for date: $current_date ==============="
    current_date=$(date -I -d "$current_date + 1 day")
    sleep 120s
done
