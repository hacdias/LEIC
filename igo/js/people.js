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

function validateNewPerson () {
  const name = document.getElementById('new-person-name').value
  const number = document.getElementById('new-person-phone').value

  if (name.length > 0 && number > 0) {
    return { name, number }
  }
}

function onNewPersonChange () {
  if (validateNewPerson()) {
    document.getElementById('new-person-submit').classList.remove('disabled')
  } else {
    document.getElementById('new-person-submit').classList.add('disabled')
  }
}

function createPerson () {
  const { name, number } = validateNewPerson()

  confirmationBox({
    question: 'Confirma a criação de um novo contacto?',
    rightHandler: () => {
      data.people.push({
        id: uuidv4(),
        name: name,
        distance: Math.floor(Math.random() * 1000) + 1,
        picture: 'maria_joao.png', // TODO: photo
        phone: numberWithSpaces(number),
        lastSeen: Math.floor(Math.random() * 60) + 1,
        messages: []
      })

      runAndBack(clearNewPerson)
    }
  })
}

function clearNewPerson () {
  document.getElementById('new-person-submit').classList.add('disabled')
  document.getElementById('new-person-name').value = ''
  document.getElementById('new-person-phone').value = ''
}

function bootstrapPeople () {
  enableKeybaordFor(document.getElementById('msg-kb'))
  enableKeybaordFor(document.getElementById('new-person-name'))
  enableKeybaordFor(document.getElementById('new-person-phone'), false)
}
