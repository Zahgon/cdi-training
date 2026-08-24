# View Components

## Fragments

The custom view components implement GUI components which are used globally.

They are Thymeleaf fragments located in `src/main/resources/templates/fragments/`, one file per component, and are
invoked with `th:replace="~{fragments/<name> :: <name>(...)}"`. The fragment parameters are positional, so every
parameter has to be passed in the order of the fragment signature; an optional parameter is passed as `null` to fall
back to its default.

### Button

Renders a link acting as a button, for executing GET requests like with a link.

Attributes:

1. `id`  
   The page unique id
2. `text`  
   The text for the button
3. `path` (Optional, Default='#')  
   The relative path from the rest application path on, or a in-page references via '#'
4. `rendered` (Optional, Default=true)  
   The rendered flag

The fragment signature is `button(id, path, text, rendered)`.

```html
<th:block th:replace="~{fragments/button :: button('toMyActionOrResource', null, 'Go to example', null)}"></th:block>
<th:block th:replace="~{fragments/button :: button('toMyActionOrResource', '#otherId', 'Go to example', null)}"></th:block>
<th:block th:replace="~{fragments/button :: button('toMyActionOrResource', '/basic/index', 'Go to example', null)}"></th:block>
```

### Link

Renders an ordinary link with a text for executing GET requests.

Attributes:

1. `id`  
   The page unique id
2. `text` (Optional)    
   The text for the link
3. `path` (Optional, Default='#')  
   The relative path from the rest application path on, or a in-page references via '#'.
4. `target` (Optional, Default='_self')  
   The link target
5. `rendered` (Optional, Default=true)  
   The rendered flag

The fragment signature is `link(id, path, text, target, rendered)`.

```html
<th:block th:replace="~{fragments/link :: link('toMyActionOrResource', null, 'Go to example', null, null)}"></th:block>
<th:block th:replace="~{fragments/link :: link('toMyActionOrResource', '#otherId', 'Go to example', null, null)}"></th:block>
<th:block th:replace="~{fragments/link :: link('toMyActionOrResource', '/basic/index', 'Go to example', '_blank', null)}"></th:block>
```

### NavLink

Renders a nav link with a text for executing GET requests from the navbar.  
The link is marked active if the user is on the current page or if the ``active`` flag ist set.

Attributes:

1. `id`  
   The page unique id
2. `path`  
   The relative path from the rest application path on
3. `text` (Optional)
   The text for the link
4. `rendered` (Optional, Default=true)  
   The rendered flag
5. `active` (Optional, Default=false)  
   The active flag marking the nav link active

The fragment signature is `navLink(id, path, text, active, rendered)`.

```html
<th:block th:replace="~{fragments/navLink :: navLink('toMyResource', '/basic/index', 'Go to example', null, null)}"></th:block>
<th:block th:replace="~{fragments/navLink :: navLink('toMyResource', '/basic/index', 'Go to example', ${pathHelper.isOnSubpage('/basic/config')}, null)}"></th:block>
```

### Card

Renders a card with a header and button section used for an element in teh index pages.

Attributes:

1. `id`  
   The unique card id within a view
2. `title`   
   The card title
3. `rendered` (Optional, default true)  
   The rendered flag

The fragment signature is `card(id, title, body, buttons, rendered)`. Its two content slots `body` and `buttons` are
fragment expression parameters which the fragment inserts with `th:replace="${body}"` and `th:replace="${buttons}"`.
The caller passes them as markup selectors: `~{:: <selector>}` picks markup out of the calling template itself, and
`~{fragments/button :: button(...)}` hands over another fragment directly. The markup below the invoking element is
replaced by the rendered card, so the selected elements only serve as the slot content.

```html
<section th:replace="~{fragments/card :: card('indexExampleCdiEvents', 'First part', ~{::#indexExampleCdiEventsBody}, ~{fragments/button :: button('toCdiEventsExample', '/basic/events', 'Go to example', null)}, null)}">
    <div id="indexExampleCdiEventsBody">
        <p>...</p>
    </div>
</section>
```
