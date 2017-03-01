(ns canboard-frontend.model)

(defprotocol Authenticatable
  (authenticate [this] "Authenticates given thing.")
  (token [this] "Returns the auth token.")
  (authenticated? [this] "Returns non-nil if given thing is authenticated"))

(defrecord User [name nickname]
  Authenticatable
  )
