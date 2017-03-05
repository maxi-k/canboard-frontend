(ns canboard-frontend.data
  (:require [reagent.core :as r :refer [atom]]
            [reagent.session :as session]))

(def ^:private initial-state
  "The initial state of the application"
  {:lang :en
   :current-user nil})

;; "The state of the application, kept in a reagent atom."
(defonce app-state
  (r/atom initial-state))

(add-watch app-state :log
           (fn [_ _ _ new]
             (.log js/console (str new))))

(defn data
  ([] (data nil))
  ([path]
   (if (or (nil? path) (empty? path))
     @app-state
     (get-in @app-state path))))

(defn data! [path data]
  (if (or (nil? path) (empty? path))
    (reset! app-state data)
    (swap! app-state assoc-in path data)))

(defn session-put!
  [key value]
  (session/put! key value))

(defn session-get [key]
  (session/get key))

(defn current-page []
  (session-get :current-page))

(defn current-page! [page]
  (session-put! :current-page page))

(def current-user
  (r/cursor app-state [:current-user]))

(def current-language
  (r/cursor app-state [:lang]))
