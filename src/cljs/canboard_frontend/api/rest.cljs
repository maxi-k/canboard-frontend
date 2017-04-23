(ns canboard-frontend.api.rest
  (:require [canboard-frontend.util :as util]
            [canboard-frontend.config :as config]
            [canboard-frontend.api.auth :as auth]
            [canboard-frontend.api.paths :as paths]
            [ajax.core :as ajax :refer [GET POST PUT DELETE PATCH]]
            [ajax.protocols :as ajaxp]
            [goog.json :as json]))

(defn fetch-boards
  "Fetches the current users boards"
  [auth-data callback]
  (auth/api-call #'GET (paths/boards-path) auth-data
                 {:handler callback
                  :error-handler callback
                  :format :json}))

(defn create-board!
  "Creates a new board with the given data."
  [data auth-data callback]
  (auth/api-call #'POST (paths/boards-path) auth-data
                 {:handler callback
                  :error-handler callback
                  :format :json
                  :params data}))

(defn delete-board!
  "Deletes the board with given id"
  [id auth-data callback]
  (auth/api-call #'DELETE (paths/board-path id) auth-data
                 {:handler callback
                  :error-handler callback}))

(defn fetch-lists
  "Fetches the lists for the board with the given id."
  [id auth-data callback]
  (auth/api-call #'GET (paths/lists-path id) auth-data
                 {:handler callback
                  :error-handler callback
                  }))

(defn delete-list!
  "Deletes the board with given id"
  [board-id list-id auth-data callback]
  (auth/api-call #'DELETE (paths/list-path board-id list-id) auth-data
                 {:handler callback
                  :error-handler callback
                  }))

(defn create-list!
  "Creates a new board with the given data."
  [data board-id auth-data callback]
  (auth/api-call #'POST (paths/lists-path board-id) auth-data
                 {:handler callback
                  :error-handler callback
                  :format :json
                  :params data}))

(defn fetch-cards
  "Fetches the cards for the list with the given id."
  [board-id list-id auth-data callback]
  (auth/api-call #'GET (paths/cards-path board-id list-id) auth-data
                 {:handler callback
                  :error-handler callback}))

(defn create-card!
  "Creates a new card with the given data."
  [data board-id list-id auth-data callback]
  (auth/api-call #'POST (paths/cards-path board-id list-id) auth-data
                 {:handler callback
                  :error-handler callback
                  :format :json
                  :params data}))

(defn delete-card!
  "Deletes the card with given id"
  [board-id list-id card-id auth-data callback]
  (auth/api-call #'DELETE (paths/card-path board-id list-id card-id) auth-data
                 {:handler callback
                  :error-handler callback}))

(defn update-card!
  "Updates the card with given board-, list- and card-id
  with the given map of key->value pairs."
  [board-id list-id card-id data auth-data callback]
  (auth/api-call #'PATCH (paths/card-path board-id list-id card-id) auth-data
                 {:handler callback
                  :error-handler callback
                  :format :json
                  :params data}))
