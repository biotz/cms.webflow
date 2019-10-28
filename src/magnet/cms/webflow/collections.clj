(ns magnet.cms.webflow.collections
  (:require [magnet.cms.core :as core]
            [magnet.cms.util :as util]
            [magnet.cms.webflow.connector])
  (:import [magnet.cms.webflow.connector Webflow]))

(defn get-all-collections [{:keys [site-id] :as wf-record}]
  (-> wf-record
      (util/do-request {:method :get
                        :url (str "/sites/" site-id "/collections")})
      (util/default-response :collections)))

(defn get-collection [wf-record collection-id]
  (-> wf-record
      (util/do-request {:method :get
                        :url (str "/collections/" collection-id)})
      (util/default-response :collection)))

(extend-protocol core/Collections
  Webflow
  (get-all-collections [this]
    (get-all-collections this))
  (get-collection [this collection-id]
    (get-collection this collection-id)))
