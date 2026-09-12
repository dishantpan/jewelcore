import re

# Fix InventoryDetails.jsx - remove remaining orphaned try/catch
with open('frontend/src/pages/InventoryDetails.jsx', 'r') as f:
    content = f.read()

# Remove orphaned try/catch block after the real loadItem function
pattern = r'\n\s+try \{\n\s+setLoading\(true\);\n\s+setError\(""\);\n\n\s+const data\s*=\n\s+await getInventoryItemById\(id\);\n\n\s+setItem\(data\);\n\n\s+\} catch \(error\) \{.*?\n\s+\}\s*\n\s*\}\s*\n'
content = re.sub(pattern, '', content, flags=re.DOTALL)

with open('frontend/src/pages/InventoryDetails.jsx', 'w') as f:
    f.write(content)
print('Fixed InventoryDetails')

# Fix Purities.jsx - add async to handleSubmit
with open('frontend/src/pages/Purities.jsx', 'r') as f:
    content = f.read()

# Fix handleSubmit to be async
if 'const handleSubmit = (event) => {' in content:
    content = content.replace('const handleSubmit = (event) => {', 'const handleSubmit = async (event) => {')
    print('Added async to handleSubmit')

with open('frontend/src/pages/Purities.jsx', 'w') as f:
    f.write(content)

print('Done')