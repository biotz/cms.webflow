(ns magnet.cms.webflow.e-commerce
  (:import [magnet.cms.webflow Webflow])
  (:require [magnet.cms.core :as core]
            [magnet.cms.util :as util]))

(defn get-all-orders [{:keys [site-id] :as wf-record}]
  (-> wf-record
      (util/do-request {:method :get
                        :url (str "/sites/" site-id "/orders")})
      (util/default-response :orders)))

(defn get-order [{:keys [site-id] :as wf-record} order-id]
  (-> wf-record
      (util/do-request {:method :get
                        :url (str "/sites/" site-id "/order/" order-id)})
      (util/default-response :order)))

(defn update-order [{:keys [site-id] :as wf-record} order-id fields]
  (-> wf-record
      (util/do-request {:method :patch
                        :url (str "/sites/" site-id "/order/" order-id)
                        :body {:fields fields}})
      (util/default-response  :order)))

(defn fulfill-order [{:keys [site-id] :as wf-record} order-id]
  (-> wf-record
      (util/do-request {:method :post
                        :url (str "/sites/" site-id "/order/" order-id "/fulfill")})
      (util/default-response :order)))

(defn unfulfill-order [{:keys [site-id] :as wf-record} order-id]
  (-> wf-record
      (util/do-request {:method :post
                        :url (str "/sites/" site-id "/order/" order-id "/unfulfill")})
      (util/default-response :order)))

(defn refund-order [{:keys [site-id] :as wf-record} order-id]
  (-> wf-record
      (util/do-request {:method :post
                        :url (str "/sites/" site-id "/order/" order-id "/refund")})
      (util/default-response :order)))

(defn get-item-inventory [wf-record collection-id item-id]
  (-> wf-record
      (util/do-request {:method :get
                        :url (str "/collections/" collection-id "/items/" item-id "/inventory")})
      (util/default-response :inventory)))

(defn update-item-inventory [wf-record collection-id item-id fields]
  (-> wf-record
      (util/do-request {:method :patch
                        :url (str "/collections/" collection-id "/items/" item-id "/inventory")
                        :body {:fields fields}})
      (util/default-response :inventory)))

(extend-protocol core/E-Commerce
  Webflow
  (get-all-orders [this]
    (get-all-orders this))
  (get-order [this order-id]
    (get-order this order-id))
  (update-order [this order-id fields]
    (update-order this order-id fields))
  (fulfill-order [this order-id]
    (fulfill-order this order-id))
  (unfulfill-order [this order-id]
    (unfulfill-order this order-id))
  (refund-order [this order-id]
    (refund-order this order-id))
  (get-item-inventory [this collection-id item-id]
    (get-item-inventory this collection-id item-id))
  (update-item-inventory [this collection-id item-id fields]
    (update-item-inventory this collection-id item-id fields)))
