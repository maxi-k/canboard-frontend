(ns canboard-frontend.api.conversion
  (:require [canboard-frontend.util :as util]))

(defn conv-card
  "Converts the data for a single card returned by an api call
  to the format used in the app-state."
  [card]
  card)

(defn conv-cards
  "Converts an collection of cards returned by an api call
  to a map {card-id -> converted-card},
  where converted-card is the format used in the app-state."
  [cards]
  (util/seq-to-map :id conv-card cards))

(defn conv-list
  "Converts the data for a single list returned by an api call
  to the format used in the app-state."
  [list]
  (update list :cards conv-cards))

(defn conv-lists
  "Converts an collection of lists returned by an api call
  to a map {list-id -> converted-list},
  where converted-list is the format used in the app-state."
  [lists]
  (util/seq-to-map :id conv-list lists))

(defn conv-board
  "Converts the data for a single board returned by an api call
  to the format used in the app-state."
  [board]
  (update board :lists conv-lists))

(defn conv-boards
  "Converts an collection of boards returned by an api call
  to a map {board-id -> converted-board},
  where converted-list is the format used in the app-state."
  [boards]
  (util/seq-to-map :id conv-board boards))
