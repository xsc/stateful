(defproject xsc/stateful "0.1.0-SNAPSHOT"
  :description "Stateful Generators for Clojure's test.check"
  :url "https://github.com/xsc/stateful-generators"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :year 2016
            :key "mit"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14" :scope "provided"]
                 [org.clojure/test.check "0.9.0" :scope "provided"]]
  :profiles {:dev {:global-vars {*warn-on-reflection* true}}
             :codox
             {:plugins [[lein-codox "0.10.2"]]
              :dependencies [[codox-theme-rdash "0.1.1"]]
              :codox {:project {:name "stateful"}
                      :metadata {:doc/format :markdown}
                      :themes [:rdash]
                      :source-uri "https://github.com/xsc/stateful/blob/master/{filepath}#L{line}"
                      :namespaces [stateful.core]}}}
  :aliases {"codox" ["with-profile" "+codox" "codox"]}
  :pedantic? :abort)
