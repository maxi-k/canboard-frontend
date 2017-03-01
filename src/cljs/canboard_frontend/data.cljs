(ns canboard-frontend.data
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]))


(def ^:private initial-state
  "The initial state of the application"
  {:lang :en
   :current-user nil})

(def app-state
  "The state of the application, kept in a reagent atom."
  (atom initial-state))

(defn session-put!
  [key value]
  (session/put! key value))

(defn session-get [key]
  (session/get key))

(defn current-page []
  (session-get :current-page))

(defn current-page! [page]
  (session-put! :current-page page))

(defn current-user []
  (@app-state :current-user))

(defn current-language []
  (@app-state :lang))
