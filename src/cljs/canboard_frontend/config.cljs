(ns canboard-frontend.config)

(def config-defaults
  "Holds a map of the default configs, which is merged with the given
  app config to produce a full app config."
  {:api-base-path "v1"
   :auth {:endpoint "v1/auth/sign_in"
          :json-root "/"
          :strategy {:type :token
                     ;; Should be either :body or :headers
                     :parameter-location :headers
                     :auth-parameters [:access-token :token-type :client :uid :expiry]}
          :implementation nil}
   ;; endpoint-strategy should be either nested or flat
   ;; determines wheters apis are called with boards/11/lists/12 or
   ;; just lists/12
   :api {:endpoint-strategy :nested}})

(def app-config
  "Holds the initial configuration parameters passed to the frontend
  by the backend through the served html <script>."
  (atom config-defaults))

(defn apply-config!
  "Applies the given configuration map to the app-config, changing it."
  [options]
  (swap! app-config merge options))

(defn assoc-config!
  "Puts the given value at given path in the config."
  [value & path]
  (swap! app-config assoc-in path value))

(defn get-config
  "Returns the value of the configuration option at given path.
   Returns nil if the path does not exist."
  [& path]
  (get-in @app-config path))
