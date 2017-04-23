(ns canboard-frontend.api.paths
  (:require [canboard-frontend.config :as config]
            [canboard-frontend.util :as util]))

(defn endpoint-strategy []
  (keyword
   (config/get-config :api :endpoint-strategy)))

(defn concat-paths
  [& paths]
  (->> paths
       (#(map util/wrap %))
       (#(apply concat %))))

(defn boards-path [] "boards")
(defn board-path [id] (concat-paths boards-path id))

(defn lists-path [board-id]
  (condp = (endpoint-strategy)
    :nested (concat-paths (board-path board-id) "lists")
    :flat "lists"))

(defn list-path [board-id list-id]
  (concat-paths (lists-path board-id) list-id))

(defn cards-path [board-id list-id]
  (condp = (endpoint-strategy)
    :nested (concat-paths (list-path board-id list-id) "cards")
    :flat "cards"))

(defn card-path [board-id list-id card-id]
  (concat-paths (cards-path board-id list-id) card-id))
