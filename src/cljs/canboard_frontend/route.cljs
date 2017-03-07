(ns canboard-frontend.route
  (:require [secretary.core :as secretary :include-macros true]
            [canboard-frontend.data :as data]
            [canboard-frontend.view :as view]))

(def pages
  "Map of path => page for client-side routing using secretary."
  {"/"             #'view/home-page
   "/boards"        #'view/boards-page})

(defn define-routes
  "Sets the default client side routes for the application"
  []

  (secretary/defroute "/" []
    (data/current-page! (pages "/")))

  (secretary/defroute "/boards" []
    (data/current-page! (pages "/boards")))

  )

(defn dispatch-view []
  (if (nil? @data/current-user)
    [view/login-page]
    [(data/current-page)]))
