import os
import matplotlib.pyplot as plt

# RUTA
directorio = r"C:\Users\saioa\OneDrive\Escritorio\ROMA_TRE\Ingegneria_dei_dati\homework_2\extracted_tables\urls_htmls_tables\all_htmls"

eliminados_por_nombre = 0
eliminados_por_tamano = 0
tamanos_eliminados_por_tamano = []

for archivo in os.listdir(directorio):
    archivo_path = os.path.join(directorio, archivo)

    # Verificar si es un archivo .html
    if archivo.endswith('.html'):
        if archivo.startswith('.'):
            print(f"Deleting file: {archivo}")
            eliminados_por_nombre += 1
            os.remove(archivo_path)
        elif os.path.getsize(archivo_path) <= 100 * 1024:
            print(f"Deleting file: {archivo}")
            eliminados_por_tamano += 1
            archivo_size_kb = os.path.getsize(archivo_path) / 1024  # Convertir tamaño a KB
            tamanos_eliminados_por_tamano.append(archivo_size_kb)
            os.remove(archivo_path)

# Metrics
print(f"Deleted files: {eliminados_por_nombre + eliminados_por_tamano}")
print(f"Deleted files because of the name: {eliminados_por_nombre}")
print(f"Deleted files because of it's size: {eliminados_por_tamano}")

# Plot 1
etiquetas = ['Because of name', 'Because of size']
valores = [eliminados_por_nombre, eliminados_por_tamano]

plt.bar(etiquetas, valores, color=['blue', 'orange'])
plt.xlabel('Deleting Criteria')
plt.ylabel('Number of files deleted')
plt.title('Files deleted')
plt.show()

output_path = os.path.join(directorio, '._plot_1.png')
plt.savefig(output_path)  
plt.close()

#PLot 2
plt.hist(tamanos_eliminados_por_tamano, bins=10, color='orange', edgecolor='black')  # Crear el histograma
plt.xlabel('Size of the deleted items (KB)')  # Etiqueta del eje X
plt.ylabel('Number of files')  # Etiqueta del eje Y
plt.title('Size distribution of the files deleted')  # Título de la gráfica
plt.show()  # Mostrar la gráfica

output_path = os.path.join(directorio, '._plot_2.png')
plt.savefig(output_path)  
plt.close()