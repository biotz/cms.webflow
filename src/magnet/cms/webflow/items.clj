(ns magnet.cms.webflow.items
  (:import [magnet.cms.webflow.connector Webflow])
  (:require [magnet.cms.core :as core]
            [magnet.cms.util :as util]
            [magnet.cms.webflow.connector]))

(defn get-items [wf-record collection-id]
  (let [{:keys [status body] :as result} (util/do-request wf-record {:method :get
                                                                     :url (str "/collections/" collection-id "/items")})]
    (if (= 200 status)
      {:success? true
       :items (:items body)}
      {:success? false
       :reason (util/default-status-codes status)
       :error-details body})))

(defn get-item [wf-record collection-id item-id]
  (let [{:keys [status body] :as result} (util/do-request wf-record {:method :get
                                                                     :url (str "/collections/" collection-id "/items/" item-id)})]
    (if (= 200 status)
      {:success? true
       :item (-> body :items first)}
      {:success? false
       :reason (util/default-status-codes status)
       :error-details body})))

(extend-protocol core/Items
  Webflow
  (get-items [this collection-id]
    (get-items this collection-id))
  (get-item [this collection-id item-id]
    (get-item this collection-id item-id)))
