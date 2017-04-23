(ns canboard-frontend.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [canboard-frontend.view :as view]
            [canboard-frontend.data :as data]
            [canboard-frontend.route :as route]
            [canboard-frontend.api.auth :as auth]
            [canboard-frontend.config :as config]))

(def pages
  "Map of path => page for client-side routing using secretary."
  {"/"              #'view/home-page
   "/boards"        #'view/boards-page
   "/boards/:id"    #'view/board-page
   "/boards/:board_id/lists/:list_id/cards/:card_id" #'view/card-detail-page})

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

  (config/assoc-config!
   (auth/make-auth-strategy (config/get-config :auth))
   :auth :implementation)
  ;; -------------------------
  ;; Initialize the view
  (mount-root))

(defn ^:export initializeApp
  "Function to be called from the html document that initializes
  the app with the options given. "
  ([] (initializeApp {}))
  ([option-map]
   (let [options (js->clj option-map :keywordize-keys true)]
     (config/apply-config! options)
     (init!))))
