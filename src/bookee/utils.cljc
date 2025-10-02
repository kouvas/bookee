(ns bookee.utils)

(defn valid-email? [email]
  (when email
    (re-matches #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$" email)))

