(ns hu.ssh.github-changelog.util
  (:require
   [clojure.string :refer [join]]
   [immuconf.config :as config]))

(def git-url (partial format "%s/%s/%s.git"))

(defn gen-sha [] (join (repeatedly 40 #(rand-nth "0123456789ABCDEF"))))

(defn str-map [f & sqs] (join (apply map f sqs)))

(defn load-config
  [& extra-files]
  (->> (map (partial str "resources/") ["config.edn" "overrides.edn"])
       (concat extra-files)
       (apply config/load)))
