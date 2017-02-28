(ns canboard-frontend.routes
  (:require [secretary.core :as secretary :include-macros true]
            [canboard-frontend.data :as data]
            [canboard-frontend.views :as views]))

(def pages
  "Map of path => page for client-side routing using secretary."
  {"/"             #'views/home-page
   "/about"        #'views/about-page})

(defn define-routes
  "Sets the default client side routes for the application"
  []

  (secretary/defroute "/" []
    (data/current-page! (pages "/")))

  (secretary/defroute "/about" []
    (data/current-page! (pages "/about")))

  )
