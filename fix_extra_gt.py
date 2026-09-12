with open('frontend/src/pages/Users.jsx', 'r') as f:
    content = f.read()

content = content.replace(
    '{formError && <div className="users-error">{formError}</div>>',
    '{formError && <div className="users-error">{formError}</div>}'
)

with open('frontend/src/pages/Users.jsx', 'w') as f:
    f.write(content)
print('Fixed extra >')