(defproject canboard-frontend "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]
                 [org.clojure/clojurescript "1.9.473" :scope "provided"]
                 [reagent "0.6.0"]
                 [reagent-utils "0.2.0"]
                 [soda-ash "0.2.0"]
                 [cljs-ajax "0.5.8"]
                 [secretary "1.2.3"]
                 [lein-doo "0.1.6"]
                 [venantius/accountant "0.1.7"
                  :exclusions [org.clojure/tools.reader]]]

  :plugins [[lein-environ "1.0.2"]
            [lein-cljsbuild "1.1.1"]
            [lein-doo "0.1.6"]
            [lein-asset-minifier "0.2.7"
             :exclusions [org.clojure/clojure]]]

  :min-lein-version "2.5.0"

  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]]

  :resource-paths ["resources" "target/cljsbuild"]

  :minify-assets
  {:assets
   {"resources/public/css/site.min.css"
    "resources/public/css/site.css"}}

  :aliases {
   "devbuild" ["do" "clean" ["cljsbuild" "once"]]
   "devloop" ["cooper" "figwheel" "sass"]
   }

  :cljsbuild
  {:builds {:min
            {:source-paths ["src/cljs" "env/prod/cljs"]
             :id "min"
             :compiler
             {:output-to "target/cljsbuild/public/js/app.min.js"
              :output-dir "target/uberjar"
              :optimizations :advanced
              :pretty-print  false}}
            :app
            {:source-paths ["src/cljs" "env/dev/cljs"]
             :compiler
             {:main "canboard-frontend.dev"
              :asset-path "/js/out"
              :output-to "target/cljsbuild/public/js/app.js"
              :output-dir "target/cljsbuild/public/js/out"
              :source-map true
              :optimizations :none
              :pretty-print  true}}
            :test
            {:source-paths ["src/cljs" "test/cljs"]
             :compiler {:main canboard-frontend.doo-runner
                        :asset-path "/js/out"
                        :output-to "target/test.js"
                        :output-dir "target/cljstest/public/js/out"
                        :optimizations :whitespace
                        :pretty-print true}}

            :devcards
            {:source-paths ["src/cljs" "env/dev/cljs"]
             :figwheel {:devcards true}
             :compiler {:main "canboard-frontend.cards"
                        :asset-path "js/devcards_out"
                        :output-to "target/cljsbuild/public/js/app_devcards.js"
                        :output-dir "target/cljsbuild/public/js/devcards_out"
                        :source-map-timestamp true
                        :optimizations :none
                        :pretty-print true}}
            }
   }


  :figwheel
  {:http-server-root "public"
   :server-port 3449
   :nrepl-port 7002
   :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]
   :css-dirs ["resources/public/css"]}


  :sass {:src "src/sass"
         :dst "resources/public/css"}

  :profiles {:dev {:repl-options {:init-ns canboard-frontend.repl
                                  :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

                   :dependencies [[figwheel-sidecar "0.5.8"]
                                  [org.clojure/tools.nrepl "0.2.12"]
                                  [com.cemerick/piggieback "0.2.2-SNAPSHOT"]
                                  [devcards "0.2.1-7"]
                                  [pjstadig/humane-test-output "0.8.1"]]

                   :source-paths ["env/dev/clj"]
                   :plugins [[lein-figwheel "0.5.8"]
                             [lein-doo "0.1.6"]
                             [cider/cider-nrepl "0.10.0-SNAPSHOT"]
                             [org.clojure/tools.namespace "0.3.0-alpha2"
                              :exclusions [org.clojure/tools.reader]]
                             [refactor-nrepl "2.0.0-SNAPSHOT"
                              :exclusions [org.clojure/clojure]]
                             [lein-sassy "1.0.7"]]

                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]

                   :env {:dev true}}})
