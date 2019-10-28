(ns magnet.cms.webflow
  (:require [integrant.core :as ig]
            [magnet.cms.webflow.collections]
            [magnet.cms.webflow.e-commerce]
            [magnet.cms.webflow.items]))

(def ^:const default-timeout
  "Default timeout value for an connection attempt with Webflow CMS API."
  2000)

(def ^:const default-max-retries
  "Default limit of attempts for Webflow request."
  10)

(def ^:const default-initial-delay
  "Initial delay for retries, specified in milliseconds."
  500)

(def ^:const default-max-delay
  "Maximun delay for a connection retry, specified in milliseconds. We
  are using truncated binary exponential backoff, with `max-delay` as
  the ceiling for the retry delay."
  1000)

(def ^:const default-backoff-ms
  [default-initial-delay default-max-delay 2.0])

(defrecord Webflow [api-token site-id timeout max-retries backoff-ms])

(defmethod ig/init-key :magnet.cms/webflow [_ {:keys [api-token site-id timeout max-retries backoff-ms]
                                                      :or {timeout default-timeout
                                                           max-retries default-max-retries
                                                           backoff-ms default-backoff-ms}}]
  (->Webflow api-token site-id timeout max-retries backoff-ms))
