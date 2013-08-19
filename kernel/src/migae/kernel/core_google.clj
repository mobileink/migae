(in-ns 'migae.kernel.core)

;(import '[java.io File FileInputStream BufferedInputStream])

(defmacro def-migae-app [app-var-name handler & [args]]
  `(def ~app-var-name ~handler))
