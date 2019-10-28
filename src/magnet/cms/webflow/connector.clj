(ns magnet.cms.webflow.connector)

(defrecord Webflow [api-token site-id timeout max-retries backoff-ms])
