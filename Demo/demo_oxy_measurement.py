import joblib
import pandas as pd
import numpy as np

mean = [155.87272727, 94.41818182, 89.72727273, 364.03636364, 43.32727273,
        61.16363636, 178.56363636, 135.4060606, 110.92727273, 21.98181818,
        39.07878788, 70.00606061, 60.07272727, 15.07272727, 20.31515152]
var = [593.16561983, 410.24330579, 438.01652893, 35.16231405, 95.49289256,
       89.95504132, 517.11867777, 551.21491284, 564.10784213, 21.06431589,
       84.3230854, 78.68481175, 73.66339762, 30.61289256, 25.41179064]
X = pd.read_csv("clinic_oxy_test.csv")
label_final = X["Target"].copy()
X = X.drop("Target", axis=1)
model = joblib.load("clinic_model_oxy.h5")
X_array = np.array(X)
X_std = (X_array - mean) / np.sqrt(var)
result = model.predict(X_std)  # Now you can obtain result of oxygen saturation measurement
