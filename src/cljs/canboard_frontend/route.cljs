(ns canboard-frontend.route
  (:require [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [canboard-frontend.data :as data]))

(defn define-routes
  "Sets the default client side routes for the application"
  [pages]

  (secretary/defroute home-route "/" []
    (data/current-page! (pages "/")))

  (secretary/defroute boards-route "/boards" []
    (data/current-page! (pages "/boards")))

  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (secretary/dispatch! path))
    :path-exists?
    (fn [path]
      (secretary/locate-route path))})
  (accountant/dispatch-current!))

(defn goto!
  "Goto the given url. Like clicking on a link"
  [loc]
  (accountant/navigate! loc))
