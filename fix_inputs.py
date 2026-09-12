with open('frontend/src/pages/Users.jsx', 'r') as f:
    content = f.read()

# Fix self-closing input tags - add /> where missing
import re
# Fix pattern: minLength={N}\n  ->  minLength={N} />\n
content = re.sub(r'(minLength=\{(\d+)\})\n', r'\1 />\n', content)
content = re.sub(r'(maxLength=\{(\d+)\})\n', r'\1 />\n', content)

with open('frontend/src/pages/Users.jsx', 'w') as f:
    f.write(content)
print('Fixed self-closing inputs')