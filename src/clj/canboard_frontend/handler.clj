(ns canboard-frontend.handler
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [canboard-frontend.middleware :refer [wrap-middleware]]
            [ring.middleware.cors :refer [wrap-cors]]
            [config.core :refer [env]]))

(def mount-target-dev
  [:div#app
   [:div.ui.active.centered.inline.loader]
   [:h3 "ClojureScript has not been compiled!"]
   [:p "please run "
    [:b "lein figwheel"]
    " in order to start the compiler"]])

(def mount-target-prod
  [:div#app
   [:div.ui.active.centered.inline.loader]])

(def mount-target
  (if (env :dev)
    mount-target-dev
    mount-target-prod))

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css "/css/vendor/semantic.min.css")
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    (include-js "/js/app.js")]))

(defn cards-page []
  (html5
   (head)
   [:body
    mount-target
    (include-js "/js/app_devcards.js")]))

(defroutes routes
  (GET "/" [] (loading-page))
  (GET "/about" [] (loading-page))
  (GET "/cards" [] (cards-page))
  (resources "/")
  (not-found "Not Found"))

(def app (wrap-cors #'routes
                    :access-control-allow-origin [#"localhost:3000"]
                    :access-control-allow-methods [:get :put :post :delete]
                    ))
