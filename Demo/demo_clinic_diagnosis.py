import tensorflow as tf
from tensorflow import keras

# load test images
demo_test = tf.keras.utils.image_dataset_from_directory("clinic_test_image",
                                                        labels=None,
                                                        shuffle=False,
                                                        image_size=(256, 256),
                                                        crop_to_aspect_ratio=True,
                                                        batch_size=1)
# load model, you can change the test model with other demo models for diagnosis test

# models including Xgboost, SVM, Logistic,Resnet50 and Inception are available for testing
model = keras.models.load_model("Clinic_Resnet50.h5")

result = model.predict(demo_test).flatten()
print(result)
# now you can obtain the diagnosis results of model prediction
