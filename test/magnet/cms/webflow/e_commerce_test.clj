(ns magnet.cms.webflow.e-commerce-test
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [magnet.cms.core :as cms]
            [magnet.cms.webflow])
  (:import [java.util UUID]))

;; Id of an order that already exists, and is fulfilled
(def ^:const test-order-id (System/getenv "WEBFLOW_TEST_ORDER_ID"))
(def ^:const test-collection-id (System/getenv "WEBFLOW_TEST_COLLECTION_ID"))
(def ^:const test-item-id (System/getenv "WEBFLOW_TEST_ITEM_ID"))

(def ^:const test-config
  {:site-id (System/getenv "WEBFLOW_TEST_SITE_ID")
   :api-token (System/getenv "WEBFLOW_TEST_API_TOKEN")})

(deftest ^:integration wrong-site-id
  (let [wrong-config (assoc test-config :site-id (str (UUID/randomUUID)))
        cms-adapter (ig/init-key :magnet.cms/webflow wrong-config)]
    (testing "Wrong site-id"
      (let [result (cms/get-all-orders cms-adapter)]
        (is (not (:success? result)))
        (is (= :bad-request (:reason result)))))))

(deftest ^:integration wrong-api-token
  (let [wrong-config (assoc test-config :api-token (str (UUID/randomUUID)))
        cms-adapter (ig/init-key :magnet.cms/webflow wrong-config)]
    (testing "Wrong api-token"
      (let [result (cms/get-all-orders cms-adapter)]
        (is (not (:success? result)))
        (is (= :access-denied (:reason result)))))))

(deftest ^:integration get-all-orders
  (let [cms-adapter (ig/init-key :magnet.cms/webflow test-config)]
    (testing "Successfull get-all-orders"
      (let [result (cms/get-all-orders cms-adapter)]
        (is (:success? result))
        (is (vector? (:orders result)))))))

(deftest ^:integration get-order
  (let [cms-adapter (ig/init-key :magnet.cms/webflow test-config)]
    (testing "Successfull get-order"
      (let [result (cms/get-order cms-adapter test-order-id)]
        (is (:success? result))
        (is (map? (:order result)))))
    (testing "Wrong order-id"
      (let [wrong-order-id (str (UUID/randomUUID))
            result (cms/get-order cms-adapter wrong-order-id)]
        (is (not (:success? result)))
        (is (= :bad-request (:reason result)))))))

(deftest ^:integration fulfill-unfulfill-order
  (let [cms-adapter (ig/init-key :magnet.cms/webflow test-config)]
    (testing "Successfull unfulfill-order"
      (let [result (cms/unfulfill-order cms-adapter test-order-id)]
        (is (:success? result))
        (is (map? (:order result)))
        (is (nil? (-> result :order :fulfilledOn)))))
    (testing "Successfull fulfill-order"
      (let [result (cms/fulfill-order cms-adapter test-order-id)]
        (is (:success? result))
        (is (map? (:order result)))
        (is (string? (-> result :order :fulfilledOn)))))
    (testing "Wrong order-id"
      (let [wrong-order-id (str (UUID/randomUUID))
            result (cms/get-order cms-adapter wrong-order-id)]
        (is (not (:success? result)))
        (is (= :bad-request (:reason result)))))))
