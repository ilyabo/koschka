(defproject koschka "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2080"]
                 [net.drib/strokes "0.5.1"]]

  :plugins [[lein-cljsbuild "1.0.0"]]

  :source-paths ["src"]

  :cljsbuild { 
    :builds [{:id "koschka"
              :source-paths ["src"]
              :compiler {
                :output-to "koschka.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
