(ns wiki-links.core
  (:gen-class)
  (:require [net.cgrand.enlive-html :as html]))

(defn fetch-page [url]
  (html/html-resource (java.net.URL. url)))

(defn -main [url]
  (try
    (let [links (->> (html/select (fetch-page url) [:a])
                     (remove #(= "external text" (-> % :attrs :class)))
                     (map #(-> % :attrs :href))
                     (filter some?)
                     (filter #(clojure.string/starts-with? % "/wiki/"))
                     (remove #(clojure.string/starts-with? % "/wiki/Datei"))
                     (remove #(clojure.string/starts-with? % "/wiki/Spezial"))
                     (remove #(clojure.string/starts-with? % "/wiki/Wikipedia"))
                     (remove #(clojure.string/starts-with? % "/wiki/Hilfe"))
                     (remove #(clojure.string/starts-with? % "/wiki/Benutzer"))
                     (remove #(clojure.string/starts-with? % "/wiki/Kategorie"))
                     (remove #(clojure.string/starts-with? % "/wiki/Portal"))
                     (remove #(clojure.string/starts-with? % "/wiki/Diskussion"))
                     (remove #(= % "/wiki/Web-Archivierung#Begrifflichkeiten"))
                     (remove #(= % "/wiki/Web-Archivierung#Begrifflichkeiten"))
                     (remove #(= % "/wiki/Internet_Archive"))
                     (remove #(clojure.string/ends-with? % "(Begriffskl%C3%A4rung)"))
                     distinct
                     shuffle
                     (map #(str "https://de.wikipedia.org/" %)))]
      (spit (last (clojure.string/split (str url) #"/")) (clojure.string/join "\n" links))
      (doseq [l links]
        (println l)))
    (catch Exception e
      (prn e))))
