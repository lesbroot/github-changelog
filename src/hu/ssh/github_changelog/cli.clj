(ns hu.ssh.github-changelog.cli
  (:require
    [clojure.tools.cli :as cli]
    [clojure.string :refer [join]]
    [hu.ssh.github-changelog.core :refer [changelog]]
    [hu.ssh.github-changelog.formatters.markdown :as mdown]
    [hu.ssh.github-changelog.util :refer [load-config]])
  (:gen-class))

(defn- min-length [min]
  [#(>= (count %) min) (format "Should be at least %s character(s)" min)])

(defn- exit [status msg]
  (println msg)
  (System/exit status))

(defn- error-msg [errors]
  (let [prefixed-errors (map #(str "   " %) errors)]
    (join \newline ["The following errors occurred while parsing your command:" "" (join \newline prefixed-errors)])))

(def cli-options
  [["-t" "--token TOKEN" "GitHub api token"
    :validate (min-length 40)]
   ["-c" "--count COUNT" "Revisions count"
    :default 1
    :parse-fn #(Integer/parseInt %)]
   ["-d" "--debug" "Turn on debug mode"]
   ["-h" "--help"]])

(defn- usage [summary]
  (join
    \newline
    ["Usage: program-name [options...] <user/repo>"
     ""
     "Options:"
     summary]))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (cli/parse-opts args cli-options)
        [_ user repo] (re-find #"^(\w+)/(\w+)$" (str (first arguments)))]
    (cond
      (:help options) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors))
      (not (and user repo)) (exit 2 (usage summary)))
    (println
     (mdown/format-tags
      (take (options :count) (changelog (merge (load-config) {:repo repo :user user})))))))
