(defproject magnet/cms.webflow "0.1.0"
  :description "A Duct library for interacting with the Webflow CMS API"
  :url "http://github.com/magnetcoop/cms.webflow"
  :license {:name "Mozilla Public License 2.0"
            :url "https://www.mozilla.org/en-US/2.0/"}
  :min-lein-version "2.9.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [integrant "0.7.0"]
                 [http-kit "2.3.0"]
                 [diehard "0.8.5"]
                 [org.clojure/data.json "0.2.6"]]
  :deploy-repositories [["snapshots" {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]
                        ["releases"  {:url "https://clojars.org/repo"
                                      :username :env/clojars_username
                                      :password :env/clojars_password
                                      :sign-releases false}]]
  :profiles {:dev {:plugins [[jonase/eastwood "0.3.4"]
                             [lein-cljfmt "0.6.2"]]}
             :repl {:repl-options {:init-ns magnet.cms.webflow
                                   :host "0.0.0.0"
                                   :port 4001}
                    :plugins [[cider/cider-nrepl "0.21.0"]]}})