(ns canboard-frontend.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [canboard-frontend.data :as data]
            [canboard-frontend.route :as route]))


(defn mount-root []
  (reagent/render-component [(fn [] (route/dispatch-view))] (.getElementById js/document "app")))

(defn init! []
  ;; -------------------------
  ;; Define routes
  (route/define-routes)

  ;; -------------------------
  ;; Initialize the view
  (mount-root))
