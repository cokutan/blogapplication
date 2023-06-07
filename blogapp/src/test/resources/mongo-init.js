db = db.getSiblingDB("test");

// Insert blog_user documents
// Insert blog documents
db.blogs.insertMany([
  {
    _id: ObjectId("000000000000000000000001"),
    title: 'Blogpost1',
    body: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut al',
    createdBy: {
      displayname: 'Ali',
      username: 'aliveli'
    },
    tags: ['First Tag']
  },
  {
    _id: ObjectId("000000000000000000000002"),
    title: 'etiam',
    body: 'Nulla facilisi. Cras non velit nec nisi vulputate nonummy. Maecenas tincidunt lacus at velit. Vivamus vel nulla eget eros elementum pellentesque. Quisque porta volutpat erat.',
    createdBy: {
      displayname: 'Wini',
      username: 'Wini'
    },
    tags: ['Second Tag']
  },
  {
    _id: ObjectId("000000000000000000000003"),
    title: 'faucibus',
    body: 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est. Donec odio justo, sollicitudin ut, suscipit a, feugiat et, eros.',
    createdBy: {
      displayname: 'Sigismundo',
      username: 'Sigismundo'
    },
    tags: ['Third Tag']
  },
  {
    _id: ObjectId("000000000000000000000004"),
    title: 'condimentum',
    body: 'Suspendisse potenti. Cras in purus eu magna vulputate luctus.',
    createdBy: {
      displayname: 'Lotty',
      username: 'Zuan'
    },
    tags: ['Fourth Tag']
  },
  {
    _id: ObjectId("000000000000000000000005"),
    title: 'felis',
    body: 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit.',
    createdBy: {
      displayname: 'Claiborn',
      username: 'Stappard'
    },
    tags: ['Fifth Tag']
  },
  {
    _id: ObjectId("000000000000000000000006"),
    title: 'pulvinar',
    body: 'Vestibulum ac est lacinia nisi venenatis tristique. Fusce congue, diam id ornare imperdiet, sapien urna pretium nisl, ut volutpat sapien arcu sed augue. Aliquam erat volutpat. In costo.',
    createdBy: {
      displayname: 'Erma',
      username: 'Straughan'
    },
    tags: ['Sixth Tag']
  },
  {
    _id: ObjectId("000000000000000000000007"),
    title: 'nisl',
    body: 'Duis bibendum. Morbi non quam nec dui luctus rutrum. Nulla tellus. In sagittis dui vel nisl. Duis ac nibh.',
    createdBy: {
      displayname: 'Siffre',
      username: 'Couper'
    },
    tags: ['Seventh Tag']
  },
  {
    _id: ObjectId("000000000000000000000008"),
    title: 'nisl',
    body: 'Maecenas rhoncus aliquam lacus. Morbi quis tortor id nulla ultrices aliquet. Maecenas leo odio, condimentum id, luctus nec, molestie sed, justo. Pellentesque viverra pede ac diam. Cras pelle.',
    createdBy: {
      displayname: 'Tomasina',
      username: 'Brownscombe'
    },
    tags: []
  },
  {
    _id: ObjectId("000000000000000000000009"),
    title: 'congue',
    body: 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est. Donec odio justo, sollicitudin ut, suscipit a, feugiat et, eros. Ves',
    createdBy: {
      displayname: 'Jacky',
      username: 'Englefield'
    },
    tags: []
  },
  {
    _id: ObjectId("000000000000000000000010"),
    title: 'orci',
    body: 'Aenean sit amet justo. Morbi ut odio. Cras mi pede, malesuada in, imperdiet et, commodo vulputate, justo. In blandit ultrices enim. Lorem ipsum dolor sit amet, consectetuer adipiscing elit.',
    createdBy: {
      displayname: 'Obidiah',
      username: 'Wooddisse'
    },
    tags: []
  },
  {
    _id: ObjectId("000000000000000000000011"),
    title: 'quis',
    body: 'Phasellus in felis. Donec semper sapien a libero. Nam dui. Proin leo odio, porttitor id, consequat in, consequat ut, nulla. Sed accumsan felis.',
    createdBy: {
      displayname: 'Felike',
      username: 'Birkinshaw'
    },
    tags: []
  }
]);

db.users.insertMany([
  { _id: ObjectId("100000000000000000000001"), displayname: 'Ali', username: 'aliveli' },
  { _id: ObjectId("100000000000000000000002"), displayname: 'Wini', username: 'Wini' },
  { _id: ObjectId("100000000000000000000003"), displayname: 'Sigismundo', username: 'Sigismundo' },
  { _id: ObjectId("100000000000000000000004"), displayname: 'Lotty', username: 'Zuan' },
  { _id: ObjectId("100000000000000000000005"), displayname: 'Claiborn', username: 'Stappard' },
  { _id: ObjectId("100000000000000000000006"), displayname: 'Erma', username: 'Straughan' },
  { _id: ObjectId("100000000000000000000007"), displayname: 'Siffre', username: 'Couper' },
  { _id: ObjectId("100000000000000000000008"), displayname: 'Tomasina', username: 'Brownscombe' },
  { _id: ObjectId("100000000000000000000009"), displayname: 'Jacky', username: 'Englefield' },
  { _id: ObjectId("100000000000000000000010"), displayname: 'Obidiah', username: 'Wooddisse' },
  { _id: ObjectId("100000000000000000000011"), displayname: 'Felike', username: 'Birkinshaw' }
]);
