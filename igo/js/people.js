/* eslint-disable no-unused-vars */
/* eslint-disable no-undef */

function getPerson (id) {
  return window.data.people.find(p => p.id === id)
}

function updatePeople (screen) {
  const people = window.data.people.sort(distanceSort)
  const content = screen.querySelector('.content')
  content.innerHTML = ''

  for (const { name, id, distance, picture } of people) {
    let el = getListTemplate(screen)

    el.dataset.args = id
    el.querySelector('.picture').src = `assets/people/${picture}`
    el.querySelector('.name').innerHTML = name
    el.querySelector('.distance').innerHTML = distance

    content.appendChild(el)
  }

  enableGoto(content)
  console.warn('TODO: add new contact')
}

function updatePersonDetails (screen, id) {
  const friend = window.data.people.find(p => p.id === id)
  updateScreenName(friend.name)

  screen.querySelector('.name').innerHTML = friend.name
  screen.querySelector('.picture').src = `assets/people/${friend.picture}`
  screen.querySelector('.phone').innerHTML = friend.phone
  screen.querySelector('.distance').innerHTML = friend.distance + 'm'
  screen.querySelector('.msg-btn').dataset.args = id
  screen.querySelector('.call-btn').dataset.args = id
}

function fillMessages (screen, id) {
  const person = getPerson(id)
  screen.querySelector('input').value = ''
  const content = screen.querySelector('.content')
  content.innerHTML = ''

  for (const { message, from } of person.messages) {
    let el = getListTemplate(screen)

    if (from) el.classList.add('from')

    el.querySelector('.picture').src = `assets/people/${person.picture}`
    el.querySelector('.message').innerHTML = message

    content.appendChild(el)
  }

  content.scrollTo(0, content.scrollHeight)
  screen.querySelector('.input-with-icon span').dataset.args = id
}

function replyMessage (el) {
  const id = el.dataset.args
  const input = el.parentElement.querySelector('input')
  const screen = el.parentElement.parentElement

  const person = getPerson(id)
  person.messages.push({ message: input.value })

  fillMessages(screen, id)
}

function fillCalls (screen, id) {
  console.warn('TODO: Fill previous calls, add call button')
}

function bootstrapPeople () {
  enableKeybaordFor(document.getElementById('msg-kb'))
}
