(ns magnet.cms.webflow.collections-test
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [magnet.cms.core :as cms])
  (:import [java.util UUID]))

(def ^:const test-config
  {:site-id (System/getenv "WEBFLOW_TEST_SITE_ID")
   :api-token (System/getenv "WEBFLOW_TEST_API_TOKEN")})

(def ^:const test-collection-id (System/getenv "WEBFLOW_TEST_COLLECTION_ID"))

(deftest ^:integration get-all-collections
  (let [cms-adapter (ig/init-key :magnet.cms/webflow test-config)]
    (testing "Get all successfully"
      (let [result (cms/get-all-collections cms-adapter)]
        (is (:success? result))
        (is (vector? (:collections result)))))))

(deftest ^:integration get-collection
  (let [cms-adapter (ig/init-key :magnet.cms/webflow test-config)]
    (testing "Get collection successfully"
      (let [result (cms/get-collection cms-adapter test-collection-id)]
        (is (:success? result))
        (is (map? (:collection result)))))
    (testing "Wrong collection-id"
      (let [wrong-collection-id (str (UUID/randomUUID))
            result (cms/get-collection cms-adapter wrong-collection-id)]
        (is (not (:success? result)))
        (is (= :bad-request (:reason result)))))))
