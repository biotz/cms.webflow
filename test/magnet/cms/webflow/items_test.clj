(ns magnet.cms.webflow.items-test
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [magnet.cms.core :as cms]
            [magnet.cms.webflow])
  (:import [java.util UUID]))

(def ^:const test-config
  {:site-id (System/getenv "WEBFLOW_TEST_SITE_ID")
   :api-token (System/getenv "WEBFLOW_TEST_API_TOKEN")})

(def ^:const test-collection-id (System/getenv "WEBFLOW_TEST_COLLECTION_ID"))
(def ^:const test-item-id (System/getenv "WEBFLOW_TEST_ITEM_ID"))

(deftest ^:integration get-all-items
  (let [cms-adapter (ig/init-key :magnet.cms/webflow test-config)]
    (testing "Get all items successfully"
      (let [result (cms/get-items cms-adapter test-collection-id)]
        (is (:success? result))
        (is (vector? (:items result)))))))

(deftest ^:integration get-item
  (let [cms-adapter (ig/init-key :magnet.cms/webflow test-config)]
    (testing "Get item successfully"
      (let [result (cms/get-item cms-adapter test-collection-id test-item-id)]
        (is (:success? result))
        (is (map? (:item result)))))
    (testing "Wrong collection-id"
      (let [wrong-collection-id (str (UUID/randomUUID))
            result (cms/get-item cms-adapter wrong-collection-id test-item-id)]
        (is (not (:success? result)))
        (is (= :bad-request (:reason result)))))
    (testing "Wrong item-id"
      (let [wrong-item-id (str (UUID/randomUUID))
            result (cms/get-item cms-adapter test-collection-id wrong-item-id)]
        (is (not (:success? result)))
        (is (= :bad-request (:reason result)))))))
