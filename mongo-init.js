db = db.getSiblingDB("blogapp");
db.createUser(
        {
            user: "cokutan",
            pwd: "123456",
            roles: [
                {
                    role: "readWrite",
                    db: "blogapp"
                }
            ]
        }
);
// Insert blog_user documents
// Insert blog documents
db.blogs.insertMany([
  {
    title: 'Blogpost1',
    body: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut al',
    createdBy: {
      displayname: 'Ali',
      username: 'aliveli'
    },
    tags: ['First Tag']
  },
  {
    title: 'etiam',
    body: 'Nulla facilisi. Cras non velit nec nisi vulputate nonummy. Maecenas tincidunt lacus at velit. Vivamus vel nulla eget eros elementum pellentesque. Quisque porta volutpat erat.',
    createdBy: {
      displayname: 'Wini',
      username: 'Wini'
    },
    tags: ['Second Tag']
  },
  {
    title: 'faucibus',
    body: 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est. Donec odio justo, sollicitudin ut, suscipit a, feugiat et, eros.',
    createdBy: {
      displayname: 'Sigismundo',
      username: 'Sigismundo'
    },
    tags: ['Third Tag']
  },
  {
    title: 'condimentum',
    body: 'Suspendisse potenti. Cras in purus eu magna vulputate luctus.',
    createdBy: {
      displayname: 'Lotty',
      username: 'Zuan'
    },
    tags: ['Forth Tag']
  },
  {
    title: 'felis',
    body: 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit.',
    createdBy: {
      displayname: 'Claiborn',
      username: 'Stappard'
    },
    tags: ['Fifth Tag']
  },
  {
    title: 'pulvinar',
    body: 'Vestibulum ac est lacinia nisi venenatis tristique. Fusce congue, diam id ornare imperdiet, sapien urna pretium nisl, ut volutpat sapien arcu sed augue. Aliquam erat volutpat. In costo.',
    createdBy: {
      displayname: 'Erma',
      username: 'Straughan'
    },
    tags: ['Sixth Tag']
  },
  {
    title: 'nisl',
    body: 'Duis bibendum. Morbi non quam nec dui luctus rutrum. Nulla tellus. In sagittis dui vel nisl. Duis ac nibh.',
    createdBy: {
      displayname: 'Siffre',
      username: 'Couper'
    },
    tags: ['Seventh Tag']
  },
  {
    title: 'nisl',
    body: 'Maecenas rhoncus aliquam lacus. Morbi quis tortor id nulla ultrices aliquet. Maecenas leo odio, condimentum id, luctus nec, molestie sed, justo. Pellentesque viverra pede ac diam. Cras pelle.',
    createdBy: {
      displayname: 'Tomasina',
      username: 'Brownscombe'
    },
    tags: []
  },
  {
    title: 'congue',
    body: 'Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla dapibus dolor vel est. Donec odio justo, sollicitudin ut, suscipit a, feugiat et, eros. Ves',
    createdBy: {
      displayname: 'Jacky',
      username: 'Englefield'
    },
    tags: []
  },
  {
    title: 'orci',
    body: 'Aenean sit amet justo. Morbi ut odio. Cras mi pede, malesuada in, imperdiet et, commodo vulputate, justo. In blandit ultrices enim. Lorem ipsum dolor sit amet, consectetuer adipiscing elit.',
    createdBy: {
      displayname: 'Obidiah',
      username: 'Wooddisse'
    },
    tags: []
  },
  {
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
  { displayname: 'Ali', username: 'aliveli' },
  { displayname: 'Wini', username: 'Wini' },
  { displayname: 'Sigismundo', username: 'Sigismundo' },
  { displayname: 'Lotty', username: 'Zuan' },
  { displayname: 'Claiborn', username: 'Stappard' },
  { displayname: 'Erma', username: 'Straughan' },
  { displayname: 'Siffre', username: 'Couper' },
  { displayname: 'Tomasina', username: 'Brownscombe' },
  { displayname: 'Jacky', username: 'Englefield' },
  { displayname: 'Obidiah', username: 'Wooddisse' },
  { displayname: 'Felike', username: 'Birkinshaw' }
]);
