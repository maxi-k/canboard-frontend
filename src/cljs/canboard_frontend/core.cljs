(ns canboard-frontend.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [canboard-frontend.view :as view]
            [canboard-frontend.data :as data]
            [canboard-frontend.route :as route]))

(def pages
  "Map of path => page for client-side routing using secretary."
  {"/"              #'view/home-page
   "/boards"        #'view/boards-page})

(defn dispatch-view
  "General function to dispatch which views should be served given the current state."
  []
  (if (or (nil? @data/current-user)
          (:unauthorized @data/current-user))
    [view/login-page]
    [(data/current-page)]))

(defn mount-root []
  (reagent/render-component [(fn [] (dispatch-view))]
                            (.getElementById js/document "app")))

(defn init! []
  ;; -------------------------
  ;; Define routes
  (route/define-routes pages)

  ;; -------------------------
  ;; Initialize the view
  (mount-root))
