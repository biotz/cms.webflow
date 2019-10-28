(ns magnet.cms.util
  (:require [clojure.data.json :as json]
            [diehard.core :as dh]
            [org.httpkit.client :as http]))

(def ^:const webflow-api-url "https://api.webflow.com")

(def ^:const gateway-timeout
  "504 Gateway timeout The server, while acting as a gateway or proxy,
  did not receive a timely response from the upstream server specified
  by the URI (e.g. HTTP, FTP, LDAP) or some other auxiliary
  server (e.g. DNS) it needed to access in attempting to complete the
  request."
  504)

(def ^:const bad-gateway
  "502 Bad gateway The server, while acting as a gateway or proxy,
  received an invalid response from the upstream server it accessed in
  attempting to fulfill the request."
  502)

(defn- fallback [value exception]
  (let [status (condp instance? exception
                 ;; Socket layer related exceptions
                 java.net.UnknownHostException :unknown-host
                 java.net.ConnectException :connection-refused
                 ;; HTTP layer related exceptions
                 org.httpkit.client.TimeoutException gateway-timeout
                 org.httpkit.client.AbortException bad-gateway)]
    {:status status}))

(defn- retry-policy [max-retries backoff-ms]
  (dh/retry-policy-from-config
   {:max-retries max-retries
    :backoff-ms backoff-ms
    :retry-on [org.httpkit.client.TimeoutException
               org.httpkit.client.AbortException]}))

(defn default-status-codes [code]
  (cond
    (keyword? code) code
    (and (>= code 200) (< code 300)) :ok
    (= code 400) :bad-request
    (or (= code 401) (= code 403)) :access-denied
    (= code 404) :not-found
    :else :error))

(defn default-response [{:keys [status body]} entity-key]
  (if (= status 200)
    {:success? true
     entity-key body}
    {:success? false
     :reason (default-status-codes status)
     :error-details body}))

(defn do-request [{:keys [api-token site-id timeout max-retries backoff-ms]} req-args]
  (let [req (-> req-args
                (assoc :oauth-token api-token
                       :headers {"accept-version" "1.0.0"}
                       :timeout timeout)
                (update :url #(str webflow-api-url %))
                (update :body #(when % (json/write-str %))))]
    (dh/with-retry {:policy (retry-policy max-retries backoff-ms)
                    :fallback fallback}
      (let [{:keys [status body error] :as resp} @(http/request req)]
        (when error
          (throw error))
        (try
          {:status status
           :body (json/read-str body :key-fn keyword :eof-error? false)}
          (catch Exception e
            {:status bad-gateway}))))))
