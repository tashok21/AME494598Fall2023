from PIL import Image
import os
import sys

def save_flipped_images(image_path, output_folder):
    # Ensure the output folder exists
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    # Load the image
    with Image.open(image_path) as img:
        # Flip the image right to left
        flipped_img = img.transpose(Image.FLIP_LEFT_RIGHT)

        # Save 5 copies of the original image
        for i in range(1, 6):
            img.save(os.path.join(output_folder, f'original_{i}.jpg'))

        # Save 5 copies of the flipped image
        for i in range(1, 6):
            flipped_img.save(os.path.join(output_folder, f'flipped_{i}.jpg'))

# Example usage
# save_flipped_images('path/to/your/image.jpg', 'path/to/output/folder')

image_path = sys.argv[1]
output_folder = sys.argv[2]

save_flipped_images(image_path, output_folder)
print("done")