(ns canboard-frontend.data
  (:require [canboard-frontend.util :as util]
            [reagent.core :as r :refer [atom]]
            [reagent.session :as session]
            [alandipert.storage-atom :refer [local-storage]]))

(def ^:private initial-state
  "The initial state of the application"
  {:lang :en
   :current-user nil
   :boards {}
   :current-board nil
   :view-data {}})

;; "The state of the application, kept in a reagent atom."
(defonce app-state
  (local-storage
   (r/atom initial-state)
   :app-storage))


;; Development Environment - log the app state when it changes
#_(add-watch app-state :log
             (fn [_ _ _ new]
               (.log js/console (str new))))
#_(set! app-state (r/atom initial-state))
#_(set! app-state (local-storage
                   (r/atom initial-state)
                   ":app-storage"))

(defn data
  "Utility function for getting data out of the app-state
  via a path-vector"
  ([] (data nil))
  ([path]
   (if (or (nil? path) (empty? path))
     @app-state
     (get-in @app-state path))))

(defn data!
  "Utility function for setting the app-state in
  the given path to given data."
  [path data]
  (if (or (nil? path) (empty? path))
    (reset! app-state data)
    (swap! app-state assoc-in path data)))

(defn reset-data!
  "Resets the app-state to the initial-state."
  []
  (.clear js/localStorage)
  (data! [] initial-state))

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
(def current-board-id (r/cursor app-state [:current-board]))
(def current-board (r/cursor
                    (fn
                      ([k] (get-in @app-state [:boards @current-board-id]))
                      ([k v] (swap! app-state assoc-in [:boards @current-board-id] v)))
                    [:current-board]))
(def boards (r/cursor app-state [:boards]))
(def lists (r/cursor current-board [:lists]))
(def view-data (r/cursor app-state [:view-data]))
(def detail-view-data (r/cursor view-data [:detail-view]))

(defn token-data
  "Returns a map of relevant token data of the current login.
  What headers are 'relevant' is defined in `util/relevant-auth-headers`."
  []
  (let [relevant util/relevant-auth-headers]
    (update
     (reduce (fn [h key] (assoc h (name key) (@current-user key))) {} relevant)
     "expiry"
     js/parseInt)))

(defn board-by-id
  "Returns the board with given id, or nil if there is none."
  [id]
  (first (filter #(== (:id %) id) @boards)))
