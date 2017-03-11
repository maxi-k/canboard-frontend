(ns canboard-frontend.data
  (:require [canboard-frontend.util :as util]
            [reagent.core :as r :refer [atom]]
            [reagent.session :as session]))

(def ^:private initial-state
  "The initial state of the application"
  {:lang :en
   :current-user nil
   :boards []
   :view-data {}})

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

(def current-user (r/cursor app-state [:current-user]))
(def current-language (r/cursor app-state [:lang]))
(def boards (r/cursor app-state [:boards]))
(def view-data (r/cursor app-state [:view-data]))

(defn token-data []
  "Returns a map of relevant token data of the current login."
  (let [relevant util/relevant-auth-headers]
    (update
     (reduce (fn [h key] (assoc h (name key) (@current-user key))) {} relevant)
     "expiry" js/parseInt)))
