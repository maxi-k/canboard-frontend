(ns canboard-frontend.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [canboard-frontend.data :as data]
              [canboard-frontend.route :as route]))


;; -------------------------
;; Define routes
(route/define-routes)

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [route/dispatch-view] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
